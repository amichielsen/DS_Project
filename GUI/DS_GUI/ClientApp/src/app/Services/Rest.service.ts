import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

  /*
start /b ssh -L 5050:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10040
start /b ssh -L 5052:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10041
start /b ssh -L 5053:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10042
start /b ssh -L 5054:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10043
start /b ssh -L 5055:dist-computing.idlab.uantwerpen.be:8080 root@dist-computing.idlab.uantwerpen.be -p 10044
*/

export class restService {


  baseUrl1 = "http://localhost:";
  namingserver = 'localhost'; //'192.168.96.6';
  baseUrl2 = "http://localhost:5050/naming";

  nodeId: string;

  constructor(private http: HttpClient) { }

  ngOnInit() { }

  get nodeIp(): string {
    return this.nodeIp;
  }
  set nodeIp(value: string) {
    this.nodeIp = value;
  }


  getNodes(): Observable<INodes> {
    return this.http.get<INodes>(this.baseUrl2 + "/hosts");
  }



  deleteNode(Id) {
    return this.http.delete(this.baseUrl2 + "/Id?Id=" + Id); //http://localhost:5050/naming/Id?Id=
    
  }
}

export interface ILocal {
  localfiles: []
}
export interface IReplica {
  replicafiles: []
}

export interface INodes {
  
}
