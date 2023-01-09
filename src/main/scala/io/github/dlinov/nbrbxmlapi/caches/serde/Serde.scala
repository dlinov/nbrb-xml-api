package io.github.dlinov.nbrbxmlapi.caches.serde

trait Serde[A, B] {
  def serialize(a: A): B

  def deserialize(b: B): Option[A]
}
