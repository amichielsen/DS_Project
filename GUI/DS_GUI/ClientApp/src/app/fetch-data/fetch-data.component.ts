import { Component, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { restService, INodes } from '../Services/Rest.service';
import { Router } from '@angular/router'


@Component({
  selector: 'app-fetch-data',
  templateUrl: './fetch-data.component.html'
})
export class FetchDataComponent {

  allNodes: INodes;
  nodeId: string;
  nodeIp: string;
  portNr: number;

  constructor(private restService: restService, private router: Router, private http: HttpClient) {
  }

  ngOnInit() {
    this.getNodes();
  }



  getNodes() {
    this.restService.getNodes().subscribe(data => {
      this.allNodes = data;
      console.log(this.allNodes);


      for (var key of Object.keys(this.allNodes)) {
        console.log(key + " -> " + this.allNodes[key])
        this.nodeId = key;
        this.nodeIp = this.allNodes[key];

      }


    }, err => {
      console.log(err);
    })
  }

  getFilesFromNode(Id: string, Ip: string) {
    this.nodeId = Id;
    this.nodeIp = "192.168." + Ip;
    console.log(this.nodeId);
    console.log(this.nodeIp);
    this.restService.nodeId = this.nodeIp;
    this.router.navigate(['/counter']);
  }

  deleteNode(Id: number) {

    if (Id == 2077) {
      this.portNr = 5052;
    }
    else if (Id == 30988) {
      this.portNr = 5053;
    }
    else if (Id == 27131) {
      this.portNr = 5054;
    }
    else { this.portNr = 5055; }



    let element = document.getElementById(String(Id));
    let hidden = element.getAttribute("hidden");
    if (!hidden) {
      element.setAttribute("hidden", "hidden");
    }
    
    this.http.delete("http://localhost:" + this.portNr +"/api/shutdown").subscribe(data => {

      console.log("nodeid " + Id + " deleted");
      

    }, err => {

      console.log(err);
      //element.removeAttribute("hidden");
      console.log("error " + Id + " not deleted");

    })
    //console.log("nodeid " + Id + " deleted");
    //this.router.navigate(['/fetch-data']);
    
    
  }
}



