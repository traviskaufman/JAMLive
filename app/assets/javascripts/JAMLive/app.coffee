###
# The Entry Point for the JAMLive! Application.
###

define [
  'jquery',
  'models/user',
  'views/create_user'
], ($, User, CreateUserView) ->
  init = ->
    user = new User()
    user.on "sync", ->
      console.log "User Connected: #{user.get 'playerId'}"

    appContainer = $ '#app'
    appContainer.append (new CreateUserView(user)).render().$el

    return

  $ init
  return
