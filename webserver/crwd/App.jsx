const {AppBar, Card, CardHeader, CardMedia} = mui;
const Styles = mui.Styles;

App = React.createClass({
    mixins: [ReactMeteorData],

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

    getMeteorData() {
        return {
            location: Locations.find(this.state.locationPosition).fetch()[0]
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
                    <Card 
                        initiallyExpanded={true} >
                        <CardHeader
                            title="Map"
                            subtitle="Singapore" 
                            actAsExpander={true} 
                            showExpandableButton={true} />
                        <CardMedia expandable={true}>
                            <CrwdMap onMarkerClick={this.onMarkerClick}/>
                        </CardMedia>
                    </Card>
                </div>
                    
                    {
                    this.state.selectedLocation ? 
                    <Location location={this.data.location}/> : ""
                    }
            </div>
        );
    }
});
