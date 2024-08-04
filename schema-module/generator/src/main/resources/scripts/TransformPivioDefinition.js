import { Schema } from "js-data";

const steckbriefSchema = new Schema({
  $schema: "http://json-schema.org/draft-04/schema#",
  $id: "/schema/pivio/document",
  title: "PivioDocument",
  type: "object",
  properties: {
    id: {
      type: "string",
      $comment:
        'in common case, id is an UUID, but this is not recommended by pivio server. "format": "uuid" ',
    },
    name: {
      type: "string",
    },
    type: {
      enum: ["service", "library", "mobile_app"],
      default: "service",
    },
    owner: {
      type: "string",
    },
    description: {
      type: "string",
    },
    short_name: {
      type: "string",
    },
    vcsroot: {
      type: "string",
    },
    links: {
      type: "object",
      properties: {
        homepage: {
          type: "string",
        },
        buildchain: {
          type: "string",
        },
        api_docs: {
          type: "string",
        },
      },
    },
    software_dependencies: {
      type: "array",
      items: {
        type: "object",
        properties: {
          name: {
            type: "string",
          },
          licenses: {
            type: "array",
            items: {
              type: "object",
              properties: {
                key: {
                  type: "string",
                },
                fullName: {
                  enum: [
                    "Commercial License",
                    "GPL-2.0",
                    "GPL-3.0",
                    "BSD-3-Clause",
                    "BSD-2-Clause",
                    "LGPL-2.0",
                    "LGPL-2.1",
                    "LGPL-3.0",
                    "MIT",
                    "MPL-2.0",
                    "CDDL-1.0",
                    "EPL-2.0",
                  ],
                },
                url: {
                  type: "string",
                  format: "uri",
                },
              },
            },
            required: ["name"],
          },
        },
      },
    },
    created: {
      type: "string",
      format: "date-time",
      readOnly: "true",
      $comment: "managed by pivio server",
    },
    lastUpload: {
      type: "string",
      format: "date-time",
      readOnly: "true",
      $comment: "managed by pivio server",
    },
    lastUpdate: {
      type: "string",
      format: "date-time",
      readOnly: "true",
      $comment: "managed by pivio server",
    },
  },
  required: ["id", "name", "type", "owner", "description", "short_name"],
});
