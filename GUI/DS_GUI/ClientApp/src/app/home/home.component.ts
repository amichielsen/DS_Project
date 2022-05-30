import { Component, Injectable } from '@angular/core';
import { restService} from '../Services/Rest.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})


export class HomeComponent {
  

  fileName: string = "";

  constructor(private restservice: restService) {

  }

  onClickSubmit(data) {

    /*var file = {
      fileName: this.fileName
    }
    console.log(file);
    this.restservice.postFileName(file).subscribe(data => {
      console.log(data);
      this.FileData = data;
      console.log(this.FileData);
    })*/

    alert("Entered file name : " + data.fileid);

  }
}
