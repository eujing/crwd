const GOOGLE_MAPS_API_KEY = "AIzaSyCVsN6mRr4QqKHNB6sOGAcCjhGmklvcuiM";

GoogleMap = React.createClass({
    propTypes: {
        name: React.PropTypes.string.isRequired,
        options: React.PropTypes.object.isRequired,
        positions: React.PropTypes.array.isRequired,
        onMarkerClick: React.PropTypes.func.isRequired,
    },

    //componentWillUpdate
    componentDidMount() {
        GoogleMaps.create({
            name: this.props.name,
            element: ReactDOM.findDOMNode(this),
            options: this.props.options
        });
    
        let props = this.props;
        GoogleMaps.ready(this.props.name, function(map) {
            for (let position of props.positions) {
                let marker = new google.maps.Marker({
                    position: new google.maps.LatLng(position.latitude, position.longitude),
                    map: map.instance
                });

                marker.addListener("click", () => {
                    props.onMarkerClick(position.latitude, position.longitude);
                });
            }
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

    propTypes: {
        onMarkerClick: React.PropTypes.func.isRequired,
    },

    componentDidMount() {
        GoogleMaps.load({key: GOOGLE_MAPS_API_KEY});
    },

    getMeteorData() {
        return {
            loaded: GoogleMaps.loaded(),
            mapOptions: GoogleMaps.loaded() && this._mapOptions(),
            locations: Locations.find({}).fetch()
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
            let markers = this.data.locations.map((loc) => {
                return {latitude: loc.latitude, longitude: loc.longitude};
            });
            return <GoogleMap
                name="crwdmap" 
                options={this.data.mapOptions} 
                positions={markers}
                onMarkerClick={this.props.onMarkerClick} />;
        }
        else {
            return <div>Loading map...</div>;
        }
    }
});
