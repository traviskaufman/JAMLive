require.config({
  paths: {
    jquery: 'lib/jquery-1.9.1.min',
    lodash: 'lib/lodash.min',
    backbone: 'lib/backbone-min'
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
