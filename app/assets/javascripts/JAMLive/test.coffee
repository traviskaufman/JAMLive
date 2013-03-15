###
# A Test File.
###

define ['jquery'], ($) ->
  Test =
    go: ->
      el = $("<h1>Hello There!!!</h1>")
      el.css 'text-align', 'center'
      el.hide()

      $(document.body).append el
      el.fadeIn()
      return

  Test
