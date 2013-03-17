define [
  'lodash',
  'backbone',
  'models/user',
  'views/user',
  'text!templates/createuser_dialog.html.ejs'
], (_, Backbone, User, UserView, CreateUserDialogTpl) ->

  ###
  # Responsible for displaying the initial dialog where a user is prompted to
  # enter an ID for him/herself before that user is saved to the server and
  # connected.
  ###
  class CreateUserView extends Backbone.View
    className: 'user-creation-dialog'

    ###
    # Contains the markup for the user creation dialog.
    ###
    template: _.template CreateUserDialogTpl

    ###
    # On Initialization, this view needs to handle the user model that will,
    # on successful validation, will be connected to the JAMLive! server and
    # passed to an actual user view.
    #
    # @param userToCreate {Object<User>} The user model this view will be
    # creating a view for.
    ###
    initialize: (userToCreate) ->
      @user = userToCreate

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
      'click .connect-user': 'connectUser'

    ###
    # Attempts to save a user to the backend. If there are any errors, they
    # will be displayed. Note that in order to see if the user connected
    # successfully a client can listen for the user's "sync" mechanism.
    ###
    connectUser: ->
      @user.save
        error: (model, xhr, options) ->
          if xhr.status is 400 and
             typeof xhr.response.error === "string"

            @$('.message').text(xhr.response.error)

    ###
    # The user that the view is attempting to save and send to an actual user
    # view.
    ###
    user: null

  CreateUserView