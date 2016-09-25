module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		sass: {
			options: {
				sourceMap: true,
				outputStyle: 'compressed'
			},
			dist: {
				files: {
					'target/classes/css/screen.css': 'src/main/resources/scss/screen.scss',
					'target/classes/css/print.css': 'src/main/resources/scss/print.scss',
				}
			}
		}
	});
	grunt.registerTask('default', ['sass:dist']);
	grunt.loadNpmTasks('grunt-sass');
};