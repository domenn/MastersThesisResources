exports.id = 'sqlite';
exports.title = 'Sqlite';
exports.color = '#e97b00';
exports.icon = 'database';
exports.group = 'Databases';
exports.input = true;
exports.output = 1;
exports.version = '1.0.0';
exports.author = 'Domen Mori';
exports.options = { dbname: "exampleSqlite3" };

exports.html = `<div class="padding">
	<div class="row">
		<div class="col-md-3">
			<div data-jc="textbox" data-jc-path="dbname" data-jc-config="align:center">@(Database name)</div>
		</div>
	</div>
</div>`;

exports.readme = `# SQLite
Work with SQLite database. Can execute queries to retrieve or store data. Sql to execute should be provided on message.sql.`;

exports.install = function(instance) {
    var node = instance;
    var sqlite3 = require("sqlite3");
    console.log("SqliteInit");

    node.doConnect = function() {
        // console.log("Sqlite connecting");
        let dbName = node.options.dbname.trim();
        if(dbName.startsWith("$(") && dbName.endsWith(")")){
            const envVarName = dbName.substring(2, dbName.length-1);
            dbName = process.env[envVarName];
            if (!dbName){
                try {
                    console.log("Provided environment variable is not defined! Unable to open database!");
                    node.error("Provided environment variable is not defined! Unable to open database!");
                }catch (e) {
                    console.log("Caught error:", e);
                }
                return
            }
        }
        node.db = node.db || new sqlite3.Database(dbName,sqlite3.OPEN_READWRITE | sqlite3.OPEN_CREATE);
        node.db.on('open', function() {
            if (node.tick) { clearTimeout(node.tick); }
            console.log("opened "+dbName+" ok");
            node.dbOpen = true;
        });
        node.db.on('error', function(err) {
            console.log("failed to open "+dbName, err);
            node.tick = setTimeout(function() { node.doConnect(); }, 2000);
        });
    };

    node.closeDb = function(){
        if (node.tick) { clearTimeout(node.tick); }
        if (node.db) {
            node.db.close();
            console.log("Sqlite DB closed.");
            node.db = null;
            node.dbOpen = false;
        }
    };

    node.doConnect();

    var doQuery = function(f_input) {
        const msg = f_input.data;
        if (typeof msg.sql === 'string') {
            sql = "select * from WeatherTable WHERE \"timestamp\" >= (select timestamp from WeatherTable WHERE datetime(\"timestamp\", 'unixepoch') < datetime('now', '-1 Hour') order by timestamp desc limit 1) order by timestamp desc;"
            if (msg.sql.length > 0) {
                bind = Array.isArray(msg.payload) ? msg.payload : [];
                node.db.all(msg.sql, bind, function(err, row) {
                    if (err) { node.error(err,msg.sql); }
                    else {
                        msg.payload = row;
                        node.send(f_input);
                    }
                });
            }
        }
        else {
            node.error("msg.sql : the query is not defined as a string",msg);
        }
    };

    instance.on('close', () => node.closeDb());


    instance.on('data', function(response) {
        if(!node.dbOpen){
            node.error("Db is not open! Please check configuration!",response);
            console.log("Db is not open! Please check configuration!",response);
        }else {
            doQuery(response);
        }
    });

    var disconnectAndConnect = function(){
        console.log("SQL disconnectAndConnect");
        node.closeDb();
        node.doConnect();
    };

    /**
     * Designer has been updated, but this instance still persists
     */
    instance.on('reinit', disconnectAndConnect);

    instance.on('options', disconnectAndConnect);
};
