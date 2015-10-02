import angular from 'angular';
import './gr-disabled.css!';

export let grDisabled = angular.module('gr.disabled', []);

grDisabled.directive('grDisabled', [function () {
    return {
        restrict: 'A',
        link: ($scope, el, attrs) => {
            const cls = 'gr-disabled';
            attrs.grDisabled !== 'false' ? el.addClass(cls) : el.removeClass(cls);
        }
    }
}]);
