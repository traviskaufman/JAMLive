define [
  'lodash',
  'backbone',
  'models/user',
  'text!templates/create_user_dialog.html.ejs'
], (_, Backbone, User, CreateUserDialogTpl) ->

  ###
  # Responsible for displaying the initial dialog where a user is prompted to
  # enter an ID for him/herself before that user is created.
  ###
  class CreateUserView extends Backbone.View
    className: 'user-creation-dialog'

    ###
    # Contains the markup for the user creation dialog.
    ###
    template: _.template CreateUserDialogTpl

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
      'click .connect-user': 'initUser'

    ###
    # Create a user and return a view that contains that user model.
    ###
    initUser: ->
      user = new User()
      # TODO: Check user connection.

  CreateUserView