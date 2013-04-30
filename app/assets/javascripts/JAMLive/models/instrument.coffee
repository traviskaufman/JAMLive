define [
  'lodash'
  'backbone'
], (_, Backbone) ->
  ###
  # This holds information about the player's instrument, including its current
  # state.
  ###
  class Instrument extends Backbone.Model
    ###
    # We make sure that that an updated param value falls within the maximum
    # and minimum allowed values for the given param.
    ###
    validate: (attributes, options) =>
      _.forEach attributes, (val, attr) ->
        return "#{attr} must be numeric" unless \
          isNaN(parseFloat(val, 10)) isnt true

    defaults:
      paramsInfo: {}

    ###
    # Here, we extract the maximum, minimum value into it's own
    # `paramsInfo` object, and then just set the default as the param value.
    ###
    parse: (response, options) ->
      pInfo = _.reduce response, (mem, val, key) ->
        mem[key] =
          min: val.min
          max: val.max

        mem
      , {}

      _.each response, (val, key, resp) ->
        resp[key] = val.default

      response.paramsInfo = pInfo

      response

  Instrument
