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


  UserView
