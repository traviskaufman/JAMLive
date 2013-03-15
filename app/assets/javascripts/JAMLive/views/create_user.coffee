define [
  'lodash',
  'models/user',
  'text!templates/create_user_dialog.html.ejs'
], (_, User, CreateUserDialogTpl) ->

  ###
  # Responsible for displaying the initial dialog where a user is prompted to
  # enter an ID for him/herself before that user is created.
  ###
  CreateUserView = Backbone.View.extend
    className: 'user-creation-dialog'

    ###
    # Contains the markup for the user creation dialog.
    ###
    template: _.template CreateUserDialogTpl

    ###
    # Render on initialization.
    ###
    initialize: ->
      @render

    ###
    # Classic Backbone-style render.
    ###
    render: =>
      @$el.html @template(@model.toJSON())

  CreateUserView