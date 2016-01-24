const {Card, CardHeader, CardMedia} = mui;

Location = React.createClass({
    mixins: [ReactMeteorData],

    propTypes: {
        location: React.PropTypes.object.isRequired,
    },

    getMeteorData() {
        return {
            sensors: Sensors.find({location: this.props.location.id}).fetch(),
        };
    },

    renderSensors() {
        return this.data.sensors.map((sensor) => {
            return <Sensor key={sensor._id} sensor={sensor} />;
        });
    },

    render() {
        //Within card
        //Graph
        //List of rendered Sensor components
        return (
            <div className="row">
                <Card
                    initiallyExpanded={true} >
                    <CardHeader
                        title={this.props.location.title}
                        subtitle="Location"
                        actAsExpander={true}
                        showExpandableButton={true} />
                    <CardMedia expandable={true}>
                        <TrendGraph
                            graphId={"loc-" + this.props.location.id}
                            rangeData={this.props.location.rangeData}
                            trendData={this.props.location.trendData}
                            timeMin={this.props.location.minTime}
                            timeMax={this.props.location.maxTime}
                            outerWidth={640}
                            outerHeight={160}
                            margin={{left:40, right:20, top:20, bottom:20}} />
                    </CardMedia>
                </Card>
                {this.renderSensors()}
            </div>
        );
    }
});
