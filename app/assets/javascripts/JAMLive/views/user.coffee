define [
  'jquery',
  'lodash',
  'backbone',
  'models/user',
  'text!templates/user_view.html.ejs',
  'jqueryui'
], ($, _, Backbone, User, UserViewTemplate) ->

  ###
  # This view handles displaying info about the user once a user is
  # instantiated.
  # @todo changeParam() user model method and refactor this code to use.
  ###
  class UserView extends Backbone.View
    className: 'container user-stage'

    template: _.template UserViewTemplate

    events:
      "mousedown .sound-key": "_onSoundKeyMouseDown"
      "touchstart .sound-key": "_onSoundKeyMouseDown"  # same deal as mousedown

    ###
    # @constructor
    ###
    initialize: ->
      @model.get('instrument').on 'sync', @render

    render: =>
      markup = @template @model.toJSON()
      _onSlideStop = @_onSlideStop

      @$el.html markup
      @$('.p-input').each ->
        $this = $(this)
        opts = {}
        data = $this.data()

        if data.max? then opts.max = data.max
        if data.min? then opts.min = data.min
        opts.step = if data.max? and data.min? then \
          (data.max - data.min) * 0.01 else 0.01
        opts.stop = _onSlideStop
        opts.value = data.default

        $this.slider opts

      @

    ###
    # When a user clicks a sound key, play that note on the server.
    # @param {Object<jQuery.Event>} evt Event object passed to handler.
    ###
    _onSoundKeyMouseDown: (evt) =>
      $target = $ evt.currentTarget
      freq = $target.data('frequency')

      @model.playNote freq

    ###
    # Update the param when the user is done setting the value.
    # @param {Object<jQuery.Event>} evt Event object passed to handler.
    ###
    _onSlideStop: (evt, ui) =>
      @model.get('instrument').set $(ui.handle.parentElement).data('name'),
                                   ui.value

  UserView
