# Fetch URLS

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/429840ee98974b7b93ae8dde07ae339d)](https://www.codacy.com/gh/jarpsimoes/cmd-cli/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jarpsimoes/cmd-cli&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/429840ee98974b7b93ae8dde07ae339d)](https://www.codacy.com/gh/jarpsimoes/cmd-cli/dashboard?utm_source=github.com&utm_medium=referral&utm_content=jarpsimoes/cmd-cli&utm_campaign=Badge_Coverage)
[![codecov](https://codecov.io/gh/jarpsimoes/cmd-cli/branch/main/graph/badge.svg?token=LFSBMSWFFM)](https://codecov.io/gh/jarpsimoes/cmd-cli)

This is a tool to test if a website is up or down.

## Prerequisites
Docker, Podman, or a local installation of Java 17.
Pull the image from docker hub:
```shell script
$ docker pull ghcr.io/jarpsimoes/cmd-cli:latest
```

## Commands Available

### Fetch
Check urls from a file or configured by arguments. Support multiple urls with 
both approaches.

#### Basic usage:
```shell script
$ docker run -v <LOCAL_PATH>:/home/jboss/.mbio_data \ 
        ghcr.io/jarpsimoes/cmd-cli:latest \
        fetch --url https://google.com
```

The file must have the following format:
*file.txt*
```text
https://google.com
https://github.com
```

Fetch urls from a file:
```shell script
$ docker run -v <LOCAL_PATH>/.mbio_data:/home/jboss/.mbio_data \ 
              -v <LOCAL_PATH>/data:/home/jboss/data \
              ghcr.io/jarpsimoes/cmd-cli:latest fetch --file data/file.txt
```

#### Grouping urls
All requests are grouped by the host and the origin. All urls configured by
argument will be grouped as "parameter" and all urls from a file will be grouped
as "filename".

Example:
```shell script
$ docker run -v <LOCAL_PATH>/.mbio_data:/home/jboss/.mbio_data \ 
              -v <LOCAL_PATH>/data:/home/jboss/data \
              ghcr.io/jarpsimoes/cmd-cli:latest \
              fetch --url https://google.com 
                    --url https://github.com \
                    --file file1.txt \
                    --file file2.txt
```

The requests google and github will be grouped as "parameter" and the requests
from file1.txt and file2.txt will be grouped as "filename".

#### Output
If the output is not configured, the default output is stored in datastore. 
The output can be configured by argument ```--o output.txt```.

```shell script
$ docker run -v <LOCAL_PATH>/.mbio_data:/home/jboss/.mbio_data \ 
              -v <LOCAL_PATH>/data:/home/jboss/data \
              ghcr.io/jarpsimoes/cmd-cli:latest fetch --file data/file.txt \
              --o output.txt
```

The output will be stored in the file output.txt.

#### Help
```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            fetch help


Usage: <main class> fetch [-hV] [-o=<output>] [-u=<url>]... [-U=<file>]...
Fetches Urls
  -h, --help              Show this help message and exit.
  -o, --output=<output>   Output file path
  -u, --url=<url>         Urls to fetch
  -U, --file=<file>       Files with urls to fetch
  -V, --version           Print version information and exit.

```

### Live
Check if a website is up or down. The command will check the website every 5 
seconds by default. The time can be configured by argument ```-i 10```. Should
be used the argument ```-l``` to limit the number of requests.

Example:
```shell script
 docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            live --url https://google.com --file ./data/file1.txt -l 10
```

#### Help:
```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            live help


Usage: <main class> live [-i=<interval>] [-l=<limit>] [-u=<url>]...
                         [-U=<file>]...
Live mode
  -i, --interval=<interval>
                        Interval in seconds
  -l, --limit=<limit>   Limit of urls to fetch
  -u, --url=<url>       Urls to fetch
  -U, --file=<file>     Files with urls to fetch
```

### History
Show the history of requests. The history is stored in datastore. The history
command support requests grouped by host, origin.

Example:
```shell
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            history -g file1.txt
```

#### Help:
```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            history help


Usage: <main class> history [-ac] [-o=<output>] [-g=<group>]... [-u=<url>]...
History mode
  -a, --all               All history
  -c, --with-content      History with content
  -g, --group=<group>     History by group
  -o, --output=<output>   Output file
  -u, --url=<url>         History by URL

```

### Backup
Backup the datastore. The backup is stored in the file with following types:
* JSON
* CSV
* TXT

Example:
```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            backup -o data/backup.json -t JSON
```

#### Help:
```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            backup help
Usage: <main class> backup -o=<output> [-t=<type>]
Backup mode
  -o, --output=<output>   Output file
  -t, --type=<type>       Backup
```

### Restore

Restore the datastore from a backup file. The backup file must be available in
the local container.

```shell script
docker run -v (PWD)/.mbio_data:/home/jboss/.mbio_data \ 
            -v (PWD)/data:/home/jboss/data \
            ghcr.io/jarpsimoes/cmd-cli:latest \
            restore help
Usage: <main class> backup -o=<output> [-t=<type>]
Backup mode
  -o, --output=<output>   Output file
  -t, --type=<type>       Backup
```


