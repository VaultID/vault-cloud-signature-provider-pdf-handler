

# Vault Cloud - Signature Provider PDF Handler #

**PDF Handler** - Preparere pdf fields/attachments before signature


**PDF Handler** - Prepara campos e anexos no PDF antes da assinatura




## License

Official AGPLv3 license available <https://www.gnu.org/licenses/agpl-3.0.html>

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```


## Licença de Uso

Tradução não oficial da AGPLv3 disponível em <http://licencas.softwarelivre.org/agpl-3.0.pt-br.html>


```
Este programa é um software livre: você pode redistribuí-lo e/ou
modificá-lo sob os termos da Licença Pública Geral Affero GNU,
conforme publicado pela Free Software Foundation, seja a versão 3
da Licença ou (a seu critério) qualquer versão posterior.

Este programa é distribuído na esperança de que seja útil,
mas SEM QUALQUER GARANTIA; sem a garantia implícita de
COMERCIALIZAÇÃO OU ADEQUAÇÃO A UM DETERMINADO PROPÓSITO. Veja a
Licença Pública Geral Affero GNU para obter mais detalhes.

Você deve ter recebido uma cópia da Licença Pública Geral Affero GNU
junto com este programa. Se não, veja <https://www.gnu.org/licenses/>.
```


## Building

Run `mvn pakage` to create the JAR file in the `target` folder.

## Running

Run with `java -jar <file>` to see the options to start, stop, and restart the service.

## Built-in API - SYNC MODE ##
| PATH | /prepare-file-fields-attachments |
|--|--|
| Method | POST |
| Headers | Content-Type: application/json |

``` 
Request
{
	"pdfFile": "/tmp/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"fields": [{
		"type": "text or image",
		"value": "text or filepath",
		"option_required": false,
		"option_multiline": false,
		"option_read_only": false,
		"option_do_not_scroll": false,
		"option_password": false,
		"border_color": "WHITE, LIGHT_GRAY, GRAY, DARK_GRAY, BLACK, RED, PINK, ORANGE, YELLOW, GREEN, MAGENTA, CYAN OR BLUE",
		"border_style": "SOLID, DASHED, BEVELED, INSET, UNDERLINE",
		"border_width": 1,
		"x": "Determines horizontal coordinate",
		"y": "Determines vertical coordinate",
		"height": "Determines the field height in pixels",
		"width": "Determines the field width in pixels",
		"page": 1
	}],
	"attachments": [{
		"file": "/tmp/certificate-attribute.pem",
		"description": "My favorite attribute certificate xpto123",
	}]
}

Response:
{
    "file": "/tmp/pdfexemplo_prepared.pdf",
    "message": "File prepareted!!!!",
    "status": "OK"
}
``` 
      
PDF Handler - Preparer to signature

## Built-in API - SYNC MODE ##
| PATH | /prepare-file-to-signature |
|--|--|
| Method | POST |
| Headers | Content-Type: application/json |

``` 
Request Sample
{
	"pdfFile": "/tmp/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"isVisibleSignature": true,
	"imageFile": "/tmp/signature.png",
	"page": 1,
	"x": 355,
	"y": 782,
	"width": 230,
	"height": 50,
	"reason": "My reason",
	"location": "My location",
	"contact": "My contact",
	"signerName": "LUAN",
	"subfilter": "adbe.pkcs7.detached",
        "type": "PdfSignature",
	"fields": [
		{
			"name": "myfavoritefield",
			"type": "text",
			"value": "I accept this document!",
			"readonly": "true"
		}
	]
}

Request Sample 2
{
	"pdfFile": "/tmp/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"isVisibleSignature": false,
	"signerName": "ACT - Soluti",
	"subfilter": "ETSI.RFC3161",
	"type": "PdfTimestampSignature"
}


Response:
{
    "file": "/tmp/pdfexemplo_prepared.pdf",
    "message": "File prepareted!!!!",
    "status": "OK"
}
``` 

## iText

This project utilizes iText under AGPLv3 licensing.

iText project: <https://github.com/itext/itext7>

iText license:

```
This program is offered under a commercial and under the AGPL license.
For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

AGPL licensing:
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

