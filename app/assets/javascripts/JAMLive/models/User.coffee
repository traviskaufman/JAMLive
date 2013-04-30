define [
  'backbone',
  'lodash',
  'models/instrument'
], (Backbone, _, Instrument) ->
  ###
  # Holds information about the current user who's
  # logged in and jamming.
  ###
  class User extends Backbone.Model
    ###
    # This is used for the sole purpose of connecting a
    # user to the JAMLive server so s/he can start transmitting
    # messages.
    ###
    url: "/connect"

    ###
    # Give the user an initially empty playerId string and show that s/he isnt
    # connected.
    ###
    defaults:
      playerId: ""
      connected: false
      instrument: new Instrument()

    ###
    # @constructor
    ###
    initialize: ->
      @listenTo @get('instrument'), "sync", @_onInstrumentSync

    ###
    # Makes sure a user's name isn't emtpy and less than 80 characters.
    # We also check to make sure the user's contains only letters, numbers, and
    # '_' symbols.
    ###
    validate: (attrs, opts) ->
      pId = attrs.playerId
      return "Must supply a nickname" unless pId.length isnt 0
      return "Must be less than 80 characters" unless pId.length < 80
      return "Must only contain letters, numbers, and '_'" unless \
        pId.match /^\w+$/g

    ###
    # Start a session on the server for the player so that s/he can actually
    # play music. This is called after the player is saved and connected
    # to the server.
    ###
    startJamSession: ->
      wsUri = document.querySelector('.app-container').dataset.wsUri
      @_connection = new WebSocket(wsUri)

      @_connection.onopen = @_onopen
      @_connection.onerror = @_onrror
      @_connection.onmessage = @_onmessage
      return

    ###
    # Send a message to the server to trigger a note on this player's behalf.
    # @param {Number} freq The frequency of the note to play.
    ###
    playNote: (freq) =>
      @_wsMsg "playNote:#{@get 'playerId'}:#{freq}"
      return

    ###
    # Change the instrument.
    # @param instName {String} The name of the instrument.
    ###
    changeInstrument: (instName) ->
      _this = @

      $.getJSON '/changeInstrument',
        pId: @get 'playerId'
        inst: instName
      , _this._onInstrumentChange

    ###
    # Sends a message to the server via a websocket.
    # @param {String} msg Message to send to the server.
    # @private
    ###
    _wsMsg: (msg) ->
      if @get('connected') is true
        @_connection.send msg
      else
        console.warn "Warning: failed to send message #{msg} due to no " +
                    "server connection"
      return

    ###
    # The WebSocket Connection Representing the player's session on the server.
    # @private
    ###
    _connection: null

    ###
    # Event handler for when socket connection is initially opened.
    ###
    _onopen: =>
      instrument = @get('instrument')

      instrument.urlRoot = "/instrument/#{@get 'playerId'}"
      instrument.fetch()

      @set 'connected', true
      console.log "Connected to server!"
      return

    ###
    # Event handler for when socket returns an error.
    # @param {Object} e Event passed to handler.
    ###
    _onerror: (e) =>
      console.error "WebSocket error: #{e.data}"
      return

    ###
    # Event handler for when client receives message from socket.
    # @param {Object} m Object containing message passed to handler.
    ###
    _onmessage: (m) =>
      console.log "WebSocket message: #{m.data}"
      return

    ###
    # Event handler for when the player's instrument is first retrieved from
    # the server.
    #
    # @param model The instrument that was just synced with the server.
    ###
    _onInstrumentSync: (model) =>
      console.log model.toJSON()
      @listenTo @get('instrument'), 'change', @_onInstrumentInit
      return

    ###
    # Called after instrument is synced so it will stay updated on the server
    # with the client.
    ###
    _onInstrumentInit: (model) =>
      _.forEach model.changedAttributes(), (val, attr) ->
        console.log "Change to #{attr} of #{val}"
        @_wsMsg "changeParam:#{@get 'playerId'}:#{attr}:#{val}"
        return
      , @

    ###
    # Called when an instrument is changed via changeInstrument after it is
    # initialized.
    ###
    _onInstrumentChange: (data, txt, jqXhr) =>
      if data is null
        console.warn "Bad instrument data: ", jqXhr.url
      else
        @get('instrument').change()
      return

  User
