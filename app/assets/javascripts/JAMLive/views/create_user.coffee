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
    # passed to an actual user view. Also binds events to the input to disable
    # or enable when entered nickname is invalid or valid, respectively.
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
    connectUser: (evt) ->
      evt.preventDefault()

      if @$(evt.target).hasClass('disabled') isnt true
        _this = @
        @user.save null,
          error: (model, xhr, options) ->
            if xhr.status is 400 and
               typeof xhr.responseText is "string"

              _this._displayMessage JSON.parse(xhr.responseText).error, 'error'
      return

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
          @_displayMessage "#{@user.get 'playerId'} is valid!", "success"

        return
      , 175
    )()

    ###
    # Displays a message with a message type.
    #
    # @private
    ###
    _displayMessage: (msgBody, msgType) ->
      $msgContainer = @$ '.control-group.input-container'
      $msgTxt = @$ '.input-container .message'
      $connectBtn = @$ '.connect-user'

      $msgContainer
        .removeClass('success')
        .removeClass('warning')
        .removeClass('error')

      $msgTxt.removeClass('invisible').text msgBody
      $msgContainer.addClass "#{msgType}"

      if msgType is 'success'
        $connectBtn.removeClass 'disabled'
      else
        $connectBtn.addClass 'disabled'

      return

  CreateUserView