const GOOGLE_MAPS_API_KEY = "AIzaSyCVsN6mRr4QqKHNB6sOGAcCjhGmklvcuiM";

GoogleMap = React.createClass({
    propTypes: {
        name: React.PropTypes.string.isRequired,
        options: React.PropTypes.object.isRequired
    },

    componentDidMount() {
        console.log("hello");

        GoogleMaps.create({
            name: this.props.name,
            element: ReactDOM.findDOMNode(this),
            options: this.props.options
        });

        GoogleMaps.ready(this.props.name, function(map) {
            var marker = new google.maps.Marker({
                position: map.options.center,
                map: map.instance
            });
        });
    },

    componentWillUnmount() {
        if (GoogleMaps.maps[this.props.name]) {
            google.maps.event.clearInstanceListeners(GoogleMaps.maps[this.props.name].instance);
            delete GoogleMaps.maps[this.props.name];
        }
    },

    render() {
        return <div className="map-container"></div>;
    }
});

CrwdMap = React.createClass({
    mixins: [ReactMeteorData],

    componentDidMount() {
        GoogleMaps.load({key: GOOGLE_MAPS_API_KEY});
    },

    getMeteorData() {
        return {
            loaded: GoogleMaps.loaded(),
            mapOptions: GoogleMaps.loaded() && this._mapOptions()
        };
    },

    _mapOptions() {
        return {
            center: new google.maps.LatLng(1.352083, 103.81983600000001), //Singapore lat long
            zoom: 8
        };
    },

    render() {
        if (this.data.loaded) {
            return <GoogleMap name="crwdmap" options={this.data.mapOptions} />;
            //return <div>Map loaded</div>;
        }
        else {
            return <div>Loading map...</div>;
        }
    }
});
