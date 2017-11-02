'use strict';

import React, {Component} from 'react';
import {
    NativeModules,
    requireNativeComponent,
    View,
    Platform
} from 'react-native';

import { PropTypes } from 'prop-types';

var NativeLiveViewManager;
var NativeLiveView;

NativeLiveViewManager = NativeModules.RNLibReStreamingModule;

class LiveView extends Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
		// NativeLiveViewManager.startPublish(() => {
		// 	alert("error");
		// }, () => {
		// 	console.log("starting publish")
		// });
		NativeLiveViewManager.init();
    }

	setNativeProps(props){
		this._root.setNativeProps(props);
	}

    componentWillUnmount() {
        NativeLiveViewManager.end();
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

	refreshPreview(){
		NativeLiveViewManager.refreshPreview();
	}

	setFocusArea(x, y, w, h){
		NativeLiveViewManager.setFocusArea(x, y, w, h);
	}

	render() {
        return <NativeLiveView ref={ref => this._root = ref} {...this.props}/>;
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
    camera:PropTypes.string,
	color:PropTypes.object,
	zoom:PropTypes.number,
	stabilize:PropTypes.bool
};

if (Platform.OS === 'android') {
    NativeLiveView = requireNativeComponent('RNLibReStreamingView', LiveView);
}
else {
    NativeLiveView = requireNativeComponent('RNVideoCoreView', LiveView);
}

export default LiveView;
