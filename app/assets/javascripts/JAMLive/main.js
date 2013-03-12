require.config({
  baseUrl: '.',
  paths: {
    'coffee-script': 'lib/coffee-script',
    'cs': 'lib/cs',
    'jquery': 'lib/jquery-1.9.0.min'
  }
});

require(['cs!app']);
