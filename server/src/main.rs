use actix_web::{App, HttpServer};

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    HttpServer::new(|| App::new())
        .bind(("0.0.0.0", 8081))?
        .run()
        .await
}
