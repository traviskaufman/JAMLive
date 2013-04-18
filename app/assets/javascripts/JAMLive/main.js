require.config({
  paths: {
    jquery: 'lib/jquery-1.9.1.min',
    lodash: 'lib/lodash.min',
    backbone: 'lib/backbone-min',
    text: 'lib/text',
    jqueryui: 'lib/jquery-ui-1.9.2.custom.min'
  },
  shim: {
    jquery: {
      exports: '$'
    },
    jqueryui: ['jquery'],
    lodash: {
      exports: '_'
    },
    backbone: {
      deps: ['jquery', 'lodash'],
      exports: 'Backbone'
    }
  }
});

require(['app']);
