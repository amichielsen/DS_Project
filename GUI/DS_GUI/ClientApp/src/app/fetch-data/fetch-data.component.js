"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.FetchDataComponent = void 0;
var core_1 = require("@angular/core");
var FetchDataComponent = /** @class */ (function () {
    function FetchDataComponent(restService, router, http) {
        this.restService = restService;
        this.router = router;
        this.http = http;
    }
    FetchDataComponent.prototype.ngOnInit = function () {
        this.getNodes();
    };
    FetchDataComponent.prototype.getNodes = function () {
        var _this = this;
        this.restService.getNodes().subscribe(function (data) {
            _this.allNodes = data;
            console.log(_this.allNodes);
            for (var _i = 0, _a = Object.keys(_this.allNodes); _i < _a.length; _i++) {
                var key = _a[_i];
                console.log(key + " -> " + _this.allNodes[key]);
                _this.nodeId = key;
                _this.nodeIp = _this.allNodes[key];
            }
        }, function (err) {
            console.log(err);
        });
    };
    FetchDataComponent.prototype.getFilesFromNode = function (Id, Ip) {
        this.nodeId = Id;
        this.nodeIp = "192.168." + Ip;
        console.log(this.nodeId);
        console.log(this.nodeIp);
        this.restService.nodeId = this.nodeIp;
        this.router.navigate(['/counter']);
    };
    FetchDataComponent.prototype.deleteNode = function (Id) {
        if (Id == 2077) {
            this.portNr = 5052;
        }
        else if (Id == 30988) {
            this.portNr = 5053;
        }
        else if (Id == 27131) {
            this.portNr = 5054;
        }
        else {
            this.portNr = 5055;
        }
        var element = document.getElementById(String(Id));
        var hidden = element.getAttribute("hidden");
        if (!hidden) {
            element.setAttribute("hidden", "hidden");
        }
        this.http.delete("http://localhost:" + this.portNr + "/api/shutdown").subscribe(function (data) {
            console.log("nodeid " + Id + " deleted");
        }, function (err) {
            console.log(err);
            //element.removeAttribute("hidden");
            console.log("error " + Id + " not deleted");
        });
        //console.log("nodeid " + Id + " deleted");
        //this.router.navigate(['/fetch-data']);
    };
    FetchDataComponent = __decorate([
        (0, core_1.Component)({
            selector: 'app-fetch-data',
            templateUrl: './fetch-data.component.html'
        })
    ], FetchDataComponent);
    return FetchDataComponent;
}());
exports.FetchDataComponent = FetchDataComponent;
//# sourceMappingURL=fetch-data.component.js.map