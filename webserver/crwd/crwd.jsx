Locations = new Mongo.Collection("locations");
Sensors = new Mongo.Collection("sensors");

if (Meteor.isClient) {

    Meteor.subscribe("locations");
    Meteor.subscribe("sensors");

    Meteor.startup(function () {
        ReactDOM.render(<App />, document.getElementById("render-target"));
    });
}

if (Meteor.isServer) {
    Meteor.publish("locations", function() {
        return Locations.find();
    });

    Meteor.publish("sensors", function() {
        return Sensors.find();
    });
}

Meteor.methods({
    //Server side queries here
});
