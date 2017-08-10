'use strict';

import React, {Component, PropTypes} from 'react';
import {
    NativeModules,
    requireNativeComponent,
    View,
    Platform
} from 'react-native';

var NativeLiveViewManager;
var NativeLiveView;

if (Platform.OS === 'android') {
    NativeLiveViewManager = NativeModules.RNLibReStreamingModule;
}
else {
    NativeLiveViewManager = NativeModules.RNVideoCoreViewManager;
}

class LiveView extends Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
		NativeLiveViewManager.startPublish(() => {
			alert("error");
		}, () => {
			console.log("starting publish")
		});
    }

    componentWillUnmount() {
        NativeLiveViewManager.stopPublish();
    }

	start(error, success){
		NativeLiveViewManager.startPublish(error, success);
	}

	stop(){
		NativeLiveViewManager.stopPublish();
	}

	swapCamera(){
		NativeLiveViewManager.swapCamera();
	}

	zoom(percent){
		NativeLiveViewManager.configureSetZoomByPercent(percent);
	}

	setSkinBlur(amount){
		NativeLiveViewManager.setSkinBlur(amount);
	}

	setWhitening(){
		NativeLiveViewManager.setWhitening();
	}

	setFishEye(){
		NativeLiveViewManager.setFishEye();
	}

	setEdgeDetection(){
		NativeLiveViewManager.setEdgeDetection();
	}

	setSeaScape(){
		NativeLiveViewManager.setSeaScape();
	}

	render() {
        return <NativeLiveView {...this.props}/>;
    }

	clearFilters(){
		NativeLiveViewManager.clearFilters();
	}
}

LiveView.propTypes = {
    ...View.propTypes,
    streamUrl:PropTypes.string,
    streamKey:PropTypes.string,
    orientation:PropTypes.string,
    quality:PropTypes.string,
    camera:PropTypes.string
};

if (Platform.OS === 'android') {
    NativeLiveView = requireNativeComponent('RNLibReStreamingView', LiveView);
}
else {
    NativeLiveView = requireNativeComponent('RNVideoCoreView', LiveView);
}

export default LiveView;
