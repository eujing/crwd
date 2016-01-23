const {AppBar, Card, CardHeader, CardMedia} = mui;
const Styles = mui.Styles;

App = React.createClass({
    childContextTypes: {
        muiTheme: React.PropTypes.object
    },

    getChildContext() {
        return {
            muiTheme: Styles.ThemeManager.getMuiTheme(Styles.LightRawTheme)
        };
    },

    getInitialState() {
        return {
            selectedLocation: false,
            locationPosition: null,
        };
    },

    onMarkerClick(latitude, longitude) {
        this.setState({
            selectedLocation: true,
            locationPosition: {
                latitude: latitude,
                longitude: longitude
            }
        });
    },

    render() {
        return (
            <div className="container">
                <div className="row">
                    <AppBar
                        title="CRWD"
                        iconClassNameRight="muidocs-icon-navigation-expand-more"
                    />
                </div>
                
                <div className="row">
                    <Card>
                        <CardHeader
                            title="Map"
                            subtitle="Singapore" />
                        <CardMedia>
                            <CrwdMap onMarkerClick={this.onMarkerClick}/>
                        </CardMedia>
                    </Card>
                </div>
                    
                    {
                    this.state.selectedLocation ? 
                    <Location location={Locations.find(this.state.locationPosition).fetch()[0]}/> : ""
                    }
            </div>
        );
    }
});
