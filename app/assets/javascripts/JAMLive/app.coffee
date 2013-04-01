###
# The Entry Point for the JAMLive! Application.
###

define [
  'jquery',
  'models/user',
  'views/create_user',
  'views/user'
], ($, User, CreateUserView, UserView) ->
  init = ->
    user = new User()
    $appEl = $ '#app'

    user.on "sync", ->
      user.startJamSession()
      userView = new UserView
        model: user

      userView.render().$el.hide()
      $appEl.html userView.$el
      userView.$el.fadeIn('fast')
      return

    $appEl.append (new CreateUserView(user)).render().$el

    return

  $ init
  return
