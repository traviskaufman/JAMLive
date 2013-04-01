define [
  'jquery',
  'lodash',
  'backbone',
  'models/user',
  'text!templates/user_view.html.ejs'
], ($, _, Backbone, User, UserViewTemplate) ->

  ###
  # This view handles displaying info about the user once a user is
  # instantiated.
  ###
  class UserView extends Backbone.View
    className: 'container user-stage'

    template: _.template UserViewTemplate

    events:
      "mousedown .sound-key": "_onSoundKeyMouseDown"
      "touchstart .sound-key": "_onSoundKeyMouseDown"  # same deal as mousedown
      "blur .p-input": "_onParamBlur"

    ###
    # @constructor
    ###
    initialize: ->
      @model.get('instrument').on 'sync', @render

    render: =>
      markup = @template @model.toJSON()
      @$el.html markup
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
    _onParamBlur: (evt) =>
      $target = $ evt.currentTarget
      name = $target.attr 'name'
      value = $target.attr 'placeholder'

      if $target.value.length > 0
        value = $target.value

      @model.get('instrument').set name, value



  UserView
