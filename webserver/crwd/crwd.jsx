if (Meteor.isClient) {
    Meteor.startup(function () {
        React.render(<App />, documentgetElementById("render-target"));
    });
}
