define ['backbone'], (Backbone) ->
  ###
  # Holds information about the current user who's
  # logged in and jamming.
  ###
  class User extends Backbone.Model
    ###
    # This is used for the sole purpose of connecting a
    # user to the JAMLive server so s/he can start transmitting
    # messages.
    ###
    url: "/connect"

    ###
    # Give the user an initially empty nickname string.
    ###
    defaults:
      nickname: ""


    ###
    # Makes sure a user's name isn't emtpy and less than 80 characters.
    # We also check to make sure the user's name is available (not already
    # taken).
    ###
    validate: ->
      return "Must be less than 80 characters" unless nickname.length < 80
      return "Must only contain letters, numbers, and '_'" unless \
        nickname.match /^\w+$/g


  User