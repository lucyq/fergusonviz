class Circle {
	float x, y;
	float radius;
	color _color;
	int index;
	int day_id;

	Circle(){};

	Circle(float x, float y, float radius, color _color, int index, int day_id) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.index = index;
		this._color = _color;
		this.day_id = day_id;
	}

	public Circle set (float x, float y, float radius, color _color, int index, int day_id) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this._color = _color;
		this.index = index;
		this.day_id = day_id;
		return this;
	}

	boolean isWithin() {
		return pow(mouseX - x, 2) + pow(mouseY - y, 2) < pow(radius, 2);
	}

	void draw() {
		fill(_color);
		ellipse(x, y, 2 * radius, 2 * radius);
	}

	void change_color(color new_color) {
		_color = new_color;
	}


}