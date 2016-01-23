Locations = new Mongo.Collection("locations");
Sensors = new Mongo.Collection("sensors");

if (Meteor.isClient) {
    Meteor.startup(function () {
        ReactDOM.render(<App />, document.getElementById("render-target"));
    });
}
