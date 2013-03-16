define ['backbone'], (Backbone) ->
  ###
  # Holds information about the current player who's
  # logged in and jamming.
  ###
  class User extends Backbone.Model
    ###
    # This is used for the sole purpose of connecting a
    # player to the JAMLive server so s/he can start transmitting
    # messages.
    ###
    url: "/connect"

    ###
    # Connect the player to the JAMLive server when s/he joins.
    #
    # @constructor
    ###
    intialize: ->
      @save()

  User