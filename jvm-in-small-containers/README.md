# Example app for tiny, tiny JVM containers
For like 128mbyte, 0.065 CPU container:

    sudo docker run -d -m 128m --cpus 0.065 --name=jvm-on-small-container -p 8080:8080 gamlerhart/blog-jvm-on-small-containers:4
    
Created for blog post: https://www.gamlor.info/wordpress/2017/04/deploying-jvm-in-tiny-containers-be-carefull