"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.restService = void 0;
var core_1 = require("@angular/core");
var restService = /** @class */ (function () {
    function restService(http) {
        this.http = http;
        this.baseUrl1 = "http://localhost:";
        this.namingserver = 'localhost'; //'192.168.96.6';
        this.baseUrl2 = "http://localhost:5050/naming";
    }
    restService.prototype.ngOnInit = function () { };
    Object.defineProperty(restService.prototype, "nodeIp", {
        get: function () {
            return this.nodeIp;
        },
        set: function (value) {
            this.nodeIp = value;
        },
        enumerable: false,
        configurable: true
    });
    restService.prototype.getNodes = function () {
        return this.http.get(this.baseUrl2 + "/hosts");
    };
    restService.prototype.deleteNode = function (Id) {
        return this.http.delete(this.baseUrl2 + "/Id?Id=" + Id); //http://localhost:5050/naming/Id?Id=
    };
    restService = __decorate([
        (0, core_1.Injectable)({
            providedIn: 'root'
        })
        /*
      start /b ssh -L 5050:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10040
      start /b ssh -L 5052:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10041
      start /b ssh -L 5053:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10042
      start /b ssh -L 5054:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10043
      start /b ssh -L 5055:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10044
      */
    ], restService);
    return restService;
}());
exports.restService = restService;
//# sourceMappingURL=Rest.service.js.map