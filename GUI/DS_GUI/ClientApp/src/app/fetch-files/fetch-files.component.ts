import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { restService, ILocal, IReplica } from '../Services/Rest.service';

@Component({
  selector: 'app-fetch-files',
  templateUrl: './fetch-files.component.html',
  styleUrls: ['./fetch-files.component.css']
})
export class FetchFilesComponent implements OnInit {

  port: Array<number> = [5052, 5053, 5054, 5055];
  localdata: string;
  replicadata: string;
  localFiles: Array<string> = [];
  replicaFiles: Array<string> = [];
  localFiles2: Array<string> = [];
  replicaFiles2: Array<string> = [];
  localFiles3: Array<string> = [];
  replicaFiles3: Array<string> = [];
  localFiles4: Array<string> = [];
  replicaFiles4: Array<string> = [];

  constructor(private restService: restService, private router: Router, private http: HttpClient) { }

  ngOnInit() {
    this.getFilesFromNodes();
  }

  getFilesFromNodes() {
    
      this.http.get("http://localhost:5052/api/localfiles", { responseType: 'text' }).subscribe(data => {
        this.localdata = data;
        //console.log(this.localdata);  //{"localfiles":[test1.txt]}
        var split = this.localdata.split("[");

        var split2 = split[1].split("]");
        this.localFiles = split2[0].split(",");
        console.log("localFiles " + this.localFiles);

      }, err => {
        console.log(err);
      })

      this.http.get("http://localhost:5052/api/replicafiles", { responseType: 'text' }).subscribe(data => {
        this.replicadata = data;
        //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
        var split = this.replicadata.split("[");
        var split2 = split[1].split("]");
        this.replicaFiles = split2[0].split(",");
        console.log("replicaFiles " + this.replicaFiles);

      }, err => {
        console.log(err);
      })


    //node 2
    this.http.get("http://localhost:5053/api/localfiles", { responseType: 'text' }).subscribe(data => {
      this.localdata = data;
      //console.log(this.localdata);  //{"localfiles":[test1.txt]}
      var split = this.localdata.split("[");

      var split2 = split[1].split("]");
      this.localFiles2 = split2[0].split(",");
      console.log("localFiles2 " + this.localFiles2);

    }, err => {
      console.log(err);
    })

    this.http.get("http://localhost:5053/api/replicafiles", { responseType: 'text' }).subscribe(data => {
      this.replicadata = data;
      //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
      var split = this.replicadata.split("[");
      var split2 = split[1].split("]");
      this.replicaFiles2 = split2[0].split(",");
      console.log("replicaFiles2 " + this.replicaFiles2);

    }, err => {
      console.log(err);
    })

    //node 3
    this.http.get("http://localhost:5054/api/localfiles", { responseType: 'text' }).subscribe(data => {
      this.localdata = data;
      //console.log(this.localdata);  //{"localfiles":[test1.txt]}
      var split = this.localdata.split("[");

      var split2 = split[1].split("]");
      this.localFiles3 = split2[0].split(",");
      console.log("localFiles3 " + this.localFiles3);

    }, err => {
      console.log(err);
    })

    this.http.get("http://localhost:5054/api/replicafiles", { responseType: 'text' }).subscribe(data => {
      this.replicadata = data;
      //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
      var split = this.replicadata.split("[");
      var split2 = split[1].split("]");
      this.replicaFiles3 = split2[0].split(",");
      console.log("replicaFiles3 " + this.replicaFiles3);

    }, err => {
      console.log(err);
    })

    //node 4
    this.http.get("http://localhost:5055/api/localfiles", { responseType: 'text' }).subscribe(data => {
      this.localdata = data;
      //console.log(this.localdata);  //{"localfiles":[test1.txt]}
      var split = this.localdata.split("[");

      var split2 = split[1].split("]");
      this.localFiles4 = split2[0].split(",");
      console.log("localFiles4 " + this.localFiles4);

    }, err => {
      console.log(err);
    })

    this.http.get("http://localhost:5055/api/replicafiles", { responseType: 'text' }).subscribe(data => {
      this.replicadata = data;
      //console.log(this.replicadata);  //{"localfiles":[test1.txt]}
      var split = this.replicadata.split("[");
      var split2 = split[1].split("]");
      this.replicaFiles4 = split2[0].split(",");
      console.log("replicaFiles4 " + this.replicaFiles4);

    }, err => {
      console.log(err);
    })
  }
}
