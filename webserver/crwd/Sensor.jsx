const {Card, CardHeader, CardMedia} = mui;

Sensor = React.createClass({
    propTypes: {
        sensor: React.PropTypes.object.isRequired
    },

    render() {
        return (
            <Card
                initiallyExpanded={false} >
                <CardHeader
                    title={this.props.sensor.title}
                    subtitle="Sensor"
                    actAsExpander={true}
                    showExpandableButton={true} />

                <CardMedia expandable={true}>
                    <TrendGraph
                        graphId={"sensor-" + this.props.sensor.id}
                        rangeData={this.props.sensor.rangeData}
                        trendData={this.props.sensor.trendData}
                        timeMin={this.props.sensor.timeMin}
                        timeMax={this.props.sensor.timeMax}
                        outerWidth={640}
                        outerHeight={480}
                        margin={{left:40, right:20, top:20, bottom:20}} />
                </CardMedia>
            </Card>
        ); 
    }
});
