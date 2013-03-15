define [Backbone], (Backbone) ->
  ###
  # Holds information about the current player who's
  # logged in and jamming.
  ###
  Player = Backbone.Model.extend

    ###
    # Initialize the player with a given id.
    #
    # @param id {String} The ID of the player.
    #
    # @constructor
    ###
    intialize: (id) ->
      @set('id', id)
      @connect

    ###
    # Default values for player attributes.
    ###
    defaults:
      id: null

    ###
    # Connects the user to the server, initializing his/her session.
    ###
    connect: ->
      endPoint = "/connect"