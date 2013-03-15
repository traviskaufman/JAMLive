require.config({
  paths: {
    zepto: 'lib/zepto.min',
    lodash: 'lib/lodash.min',
    backbone: 'lib/backbone-min'
  },
  shim: {
    zepto: {
      exports: '$'
    },
    lodash: {
      exports: '_'
    },
    backbone: {
      deps: ['zepto', 'lodash'],
      exports: 'Backbone'
    }
  }
});

require(['app']);
