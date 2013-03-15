###
# The Entry Point for the JAMLive! Application.
###

define [
  'jquery',
  'views/create_user'
], ($, CreateUserView) ->
  init = ->
    appContainer = $ '#app'
    appContainer.append (new CreateUserView()).render().$el
    return

  $ init
  return
