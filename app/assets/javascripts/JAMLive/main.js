require.config({
  paths: {
    jquery: 'lib/jquery-1.9.1.min',
    lodash: 'lib/lodash.min',
    backbone: 'lib/backbone-min',
    text: 'lib/text'
  },
  shim: {
    jquery: {
      exports: '$'
    },
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
