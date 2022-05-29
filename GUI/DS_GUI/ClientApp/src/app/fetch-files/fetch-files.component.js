"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.FetchFilesComponent = void 0;
var core_1 = require("@angular/core");
var FetchFilesComponent = /** @class */ (function () {
    function FetchFilesComponent(restService, router, http) {
        this.restService = restService;
        this.router = router;
        this.http = http;
        this.port = [5052, 5053, 5054, 5055];
        this.localFiles = [];
        this.replicaFiles = [];
        this.localFiles2 = [];
        this.replicaFiles2 = [];
        this.localFiles3 = [];
        this.replicaFiles3 = [];
        this.localFiles4 = [];
        this.replicaFiles4 = [];
    }
    FetchFilesComponent.prototype.ngOnInit = function () {
        this.getFilesFromNodes();
    };
    FetchFilesComponent.prototype.getFilesFromNodes = function () {
        var _this = this;
        this.http.get("http://localhost:5052/api/localfiles", { responseType: 'text' }).subscribe(function (data) {
            _this.localdata = data;
            //console.log(this.localdata);  //{"localfiles":[test1.txt]}
            var split = _this.localdata.split("[");
            var split2 = split[1].split("]");
            _this.localFiles = split2[0].split(",");
            console.log("localFiles " + _this.localFiles);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5052/api/replicafiles", { responseType: 'text' }).subscribe(function (data) {
            _this.replicadata = data;
            //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
            var split = _this.replicadata.split("[");
            var split2 = split[1].split("]");
            _this.replicaFiles = split2[0].split(",");
            console.log("replicaFiles " + _this.replicaFiles);
        }, function (err) {
            console.log(err);
        });
        //node 2
        this.http.get("http://localhost:5053/api/localfiles", { responseType: 'text' }).subscribe(function (data) {
            _this.localdata = data;
            //console.log(this.localdata);  //{"localfiles":[test1.txt]}
            var split = _this.localdata.split("[");
            var split2 = split[1].split("]");
            _this.localFiles2 = split2[0].split(",");
            console.log("localFiles2 " + _this.localFiles2);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5053/api/replicafiles", { responseType: 'text' }).subscribe(function (data) {
            _this.replicadata = data;
            //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
            var split = _this.replicadata.split("[");
            var split2 = split[1].split("]");
            _this.replicaFiles2 = split2[0].split(",");
            console.log("replicaFiles2 " + _this.replicaFiles2);
        }, function (err) {
            console.log(err);
        });
        //node 3
        this.http.get("http://localhost:5054/api/localfiles", { responseType: 'text' }).subscribe(function (data) {
            _this.localdata = data;
            //console.log(this.localdata);  //{"localfiles":[test1.txt]}
            var split = _this.localdata.split("[");
            var split2 = split[1].split("]");
            _this.localFiles3 = split2[0].split(",");
            console.log("localFiles3 " + _this.localFiles3);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5054/api/replicafiles", { responseType: 'text' }).subscribe(function (data) {
            _this.replicadata = data;
            //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
            var split = _this.replicadata.split("[");
            var split2 = split[1].split("]");
            _this.replicaFiles3 = split2[0].split(",");
            console.log("replicaFiles3 " + _this.replicaFiles3);
        }, function (err) {
            console.log(err);
        });
        //node 4
        this.http.get("http://localhost:5055/api/localfiles", { responseType: 'text' }).subscribe(function (data) {
            _this.localdata = data;
            //console.log(this.localdata);  //{"localfiles":[test1.txt]}
            var split = _this.localdata.split("[");
            var split2 = split[1].split("]");
            _this.localFiles4 = split2[0].split(",");
            console.log("localFiles4 " + _this.localFiles4);
        }, function (err) {
            console.log(err);
        });
        this.http.get("http://localhost:5055/api/replicafiles", { responseType: 'text' }).subscribe(function (data) {
            _this.replicadata = data;
            //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
            var split = _this.replicadata.split("[");
            var split2 = split[1].split("]");
            _this.replicaFiles4 = split2[0].split(",");
            console.log("replicaFiles4 " + _this.replicaFiles4);
        }, function (err) {
            console.log(err);
        });
    };
    FetchFilesComponent = __decorate([
        (0, core_1.Component)({
            selector: 'app-fetch-files',
            templateUrl: './fetch-files.component.html',
            styleUrls: ['./fetch-files.component.css']
        })
    ], FetchFilesComponent);
    return FetchFilesComponent;
}());
exports.FetchFilesComponent = FetchFilesComponent;
//# sourceMappingURL=fetch-files.component.js.map