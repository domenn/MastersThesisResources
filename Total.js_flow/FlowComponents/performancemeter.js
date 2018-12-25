exports.id = 'performancemeter';
exports.title = 'Performance meter';
exports.color = '#6ac4e9';
exports.icon = 'tachometer-alt-fast';
exports.group = 'Common';
exports.input = true;
exports.output = 1;
exports.version = '1.0.0';
exports.author = 'Domen Mori';
exports.options = { measureat: 3000 };

exports.html = `<div class="padding">
	<div class="row">
		<div class="col-md-3">
			<div data-jc="textbox" data-jc-path="measureat" data-jc-config="align:stretch">@(Measure at)</div>
		</div>
	</div>
</div>`;

exports.readme = `# Performance meter
My personal thing for performance measuring.`;

exports.install = function(instance) {
    var node = instance;
    var module = require("../../otherLanguageHelpers/nodejs_testapp/mqtt_benchmarker/utils/performance_meter");

    var initializer = function(){
        module.initialize(Number(instance.options.measureat));
    };

    // instance.on('close', () => node.closeDb());

    instance.on('data', function(flowdata) {
        module.notifyEvent();
        instance.send(0, flowdata);
    });

    /**
     * Designer has been updated, but this instance still persists
     */
    instance.on('reinit', initializer);

    instance.on('options', initializer);
};
