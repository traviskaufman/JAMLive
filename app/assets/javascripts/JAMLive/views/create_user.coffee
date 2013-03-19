define [
  'lodash',
  'backbone',
  'models/user',
  'views/user',
  'text!templates/create_user_dialog.html.ejs'
], (_, Backbone, User, UserView, CreateUserDialogTpl) ->

  ###
  # Responsible for displaying the initial dialog where a user is prompted to
  # enter an ID for him/herself before that user is saved to the server and
  # connected.
  ###
  class CreateUserView extends Backbone.View
    className: 'user-creation-dialog'

    ###
    # The user that the view is attempting to save and send to an actual user
    # view.
    ###
    user: null

    ###
    # Contains the markup for the user creation dialog.
    ###
    template: _.template CreateUserDialogTpl

    ###
    # On Initialization, this view needs to handle the user model that will,
    # on successful validation, will be connected to the JAMLive! server and
    # passed to an actual user view. Also binds events to this user to check
    # for valid / invalid model.
    #
    # @param userToCreate {Object<User>} The user model this view will be
    # creating a view for.
    ###
    initialize: (userToCreate) ->
      @user = userToCreate
      return

    ###
    # Classic Backbone-style render.
    ###
    render: =>
      @$el.html @template()
      @

    ###
    # Events bound to this view.
    ###
    events:
      'keyup .pId-input': '_attemptSetPlayerId'
      'click .connect-user': 'connectUser'

    ###
    # Attempts to save a user to the backend. If there are any errors, they
    # will be displayed. Note that in order to see if the user connected
    # successfully a client can listen for the user's "sync" event.
    ###
    connectUser: ->
      @user.save
        error: (model, xhr, options) ->
          if xhr.status is 400 and
             typeof xhr.response.error is "string"

            @_displayMessage xhr.response.error, 'error'

    ###
    # Runs validate() on the user model and displays any errors if the user
    # does not pass validation. Otherwise it will display a success message.
    #
    # @param {Object<jQuery.Event>} evt The event object passed on when this
    # method is triggered as the result of an event being called.
    #
    # @private
    ###
    _attemptSetPlayerId: ( =>
      # Make sure evt callback is bound to correct receiver.
      _.debounce (evt) ->
        error = @user.validate
                  playerId: evt.target.value

        if typeof error isnt "undefined"
          @_displayMessage "#{error}", 'error'
        else
          @user.set 'playerId', evt.target.value
          @_displayMessage "#{@user.get 'playerId'} available!", "success"

        return
      , 175
    )()

    ###
    # Displays a message with a message type.
    #
    # @private
    ###
    _displayMessage: (msgBody, level) ->
      $msgEl = @$('.message')
      $msgEl
        .removeClass('hidden')
        .removeClass('alert-success')
        .removeClass('alert-warning')
        .removeClass('alert-error')

      $msgEl.text(msgBody)
      $msgEl.addClass("alert-#{level}")

  CreateUserView