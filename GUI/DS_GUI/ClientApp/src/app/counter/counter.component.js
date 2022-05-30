"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.CounterComponent = void 0;
var core_1 = require("@angular/core");
var CounterComponent = /** @class */ (function () {
    function CounterComponent(restService, http) {
        this.restService = restService;
        this.http = http;
        this.nodeId = this.restService.nodeId;
        this.option = {
            title: {
                text: 'Network Graph'
            },
            tooltip: {},
            animationDurationUpdate: 1500,
            animationEasingUpdate: 'quinticInOut',
            series: [
                {
                    type: 'graph',
                    layout: 'none',
                    symbolSize: 50,
                    roam: true,
                    label: {
                        show: true
                    },
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [4, 10],
                    edgeLabel: {
                        fontSize: 20
                    },
                    data: [
                        {
                            name: 'node 2077',
                            x: 300,
                            y: 300
                        },
                        {
                            name: 'Node 30988',
                            x: 800,
                            y: 300
                        },
                        {
                            name: 'Node 27131',
                            x: 550,
                            y: 100
                        },
                        {
                            name: 'Node 4',
                            x: 550,
                            y: 500
                        }
                    ],
                    // links: [],
                    links: [
                        {
                            source: 0,
                            target: 1,
                            symbolSize: [5, 20],
                            label: {
                                show: true
                            },
                            lineStyle: {
                                width: 5,
                                curveness: 0.2
                            }
                        },
                        {
                            source: 'Node 2',
                            target: 'Node 1',
                            label: {
                                show: true
                            },
                            lineStyle: {
                                curveness: 0.2
                            }
                        },
                        {
                            source: 'Node 1',
                            target: 'Node 3'
                        },
                        {
                            source: 'Node 2',
                            target: 'Node 3'
                        },
                        {
                            source: 'Node 2',
                            target: 'Node 4'
                        },
                        {
                            source: 'Node 1',
                            target: 'Node 4'
                        }
                    ],
                    lineStyle: {
                        opacity: 0.9,
                        width: 2,
                        curveness: 0
                    }
                }
            ]
        };
    }
    CounterComponent.prototype.ngOnInit = function () {
        this.getNeighbours();
    };
    CounterComponent.prototype.getNeighbours = function () {
        var _this = this;
        this.http.get("http://localhost:5052/api/neighbours").subscribe(function (data) {
            _this.neighbourdata = data;
            console.log(_this.neighbourdata);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5053/api/neighbours").subscribe(function (data) {
            _this.neighbourdata2 = data;
            console.log(_this.neighbourdata2);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5054/api/neighbours").subscribe(function (data) {
            _this.neighbourdata3 = data;
            console.log(_this.neighbourdata3);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5055/api/neighbours").subscribe(function (data) {
            _this.neighbourdata4 = data;
            console.log(_this.neighbourdata4);
        }, function (err) {
            console.log(err);
        });
        this.option;
    };
    CounterComponent = __decorate([
        (0, core_1.Component)({
            selector: 'app-counter-component',
            templateUrl: './counter.component.html'
        })
    ], CounterComponent);
    return CounterComponent;
}());
exports.CounterComponent = CounterComponent;
//# sourceMappingURL=counter.component.js.map