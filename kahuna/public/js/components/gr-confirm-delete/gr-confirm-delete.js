import angular from 'angular';
import './gr-confirm-delete.css!';

export const confirmDelete = angular.module('gr.confirmDelete', []);

confirmDelete.directive('grConfirmDelete', ['$timeout', 'onValChange', function($timeout, onValChange) {

    return {
        restrict: 'E',
        transclude: true,
        template: `
            <button class="gr-confirm-delete" type="button"
                ng:click="onClick()"
                ng:disabled="grDisabled"
                ng:class="{'gr-confirm-delete--confirm': showConfirm}">
                <gr-icon-label ng:if="!showConfirm" gr-icon="delete">{{label}}</gr-icon-label>
                <gr-icon-label ng:if="showConfirm" gr-icon="delete">Confirm Delete</gr-icon-label>
            </button>`,

        link: function(scope, element, attrs) {
            const onChange = () => scope.$eval(attrs.grOnConfirm);

            scope.label = attrs.grLabel || 'Delete';
            scope.disabled = attrs.grDisabled || false;

            attrs.$observe('grDisabled', function (val) {
                console.log(val);
            });

            scope.onClick = () => {
                if (! scope.disabled) {
                    scope.showConfirm = true;
                    element.on('click', function() {
                        element.on('click', onChange);
                        $timeout(() => {
                            element.off('click', onChange);
                            scope.showConfirm = false;
                        }, 5000);
                    });
                }
            };

        }
    };

}]);
