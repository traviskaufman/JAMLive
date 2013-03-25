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
      window.testUser = user
      console.log "Now you can access window.testUser!!"
      return

    appContainer = $ '#app'
    appContainer.append (new CreateUserView(user)).render().$el

    return

  $ init
  return
