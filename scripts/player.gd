extends CharacterBody2D

var dir: Vector2 = Vector2.ZERO
@export var speed: int = 200
@export var jump_velocity: float = -300.0

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass
# Called every frame. 'delta' is the elapsed time since the previous frame.
func _physics_process(_delta: float) -> void:
	dir = Input.get_vector("left", "right", "up", "down")
	velocity = dir * speed
	move_and_slide()
