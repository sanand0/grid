import angular from 'angular';
import template from './gr-image-usage.html!text';
import './gr-image-usage.css!';

export let module = angular.module('gr.imageUsage', []);

module.controller('grImageUsageCtrl', ['mediaUsage', function (mediaUsage) {
    let ctrl = this;

    mediaUsage.getUsage(ctrl.image).then(data => {
        ctrl.usage = data;
    });
}]);

module.directive('grImageUsage', [function() {

    return {
        restrict: 'E',
        template: template,
        controller: 'grImageUsageCtrl',
        controllerAs: 'ctrl',
        bindToController: true,
        transclude: true,
        scope: {
            image: '=grImage'
        }
    };

}]);
