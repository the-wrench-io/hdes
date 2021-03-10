import { Ast } from './Ast';
import createContext, { ImmutableShapeIndex } from './AstShapeVisitorContext';


class ImmutableShapeView implements Ast.ShapeView {
  private _lines: Ast.Line[];
  private _shapes: Record<string, Ast.Shape>;
  
  constructor(lines: Ast.Line[], shapes: Record<string, Ast.Shape>) {
    this._lines = lines;
    this._shapes = shapes;
  }
  get lines() {
    return this._lines;
  }
  get shapes() {
    return this._shapes;
  }
}

class ImmutableCord implements Ast.Cord {
  private _x: number; 
  private _y: number;
  constructor(x: number, y: number) {
    this._x = x;
    this._y = y;
  }  
  get x() {
    return this._x;
  }
  get y() {
    return this._y;
  }
}

class RootShape implements Ast.Shape {
  private _id: string; 
  private _size: Ast.NodeSize; 
  private _center: Ast.Cord;
  private _left: Ast.Cord;
  private _right: Ast.Cord;
  private _top: Ast.Cord; 
  private _bottom: Ast.Cord;
    
  constructor(start: Ast.Cord) {
    this._id = "root";
    this._center = new ImmutableCord(start.x, start.y);
    this._size = {height: 0, width: 0};
    this._left = this._center;
    this._right = this._center;
    this._top = this._center;
    this._bottom = this._center;
  }  
  get id() {
    return this._id;
  }
  get size() {
    return this._size;
  }
  get center() {
    return this._center;
  }
  get left() {
    return this._left;
  }
  get right() {
    return this._right;
  }
  get top() {
    return this._top;
  }
  get bottom() {
    return this._bottom;
  }
}

class ImmutableShape implements Ast.Shape {
  private _id: string; 
  private _size: Ast.NodeSize; 
  private _center: Ast.Cord;
  private _left: Ast.Cord;
  private _right: Ast.Cord;
  private _top: Ast.Cord; 
  private _bottom: Ast.Cord;
    
  constructor(id: string, size: Ast.NodeSize, start: Ast.Cord) {
    this._id = id;
    
    const rx = size.width/2;
    const ry = size.height/2;
    this._size = size;

    this._center = new ImmutableCord(start.x, start.y + ry);
    this._left =   new ImmutableCord(this._center.x-rx, this._center.y);
    this._right =  new ImmutableCord(this._center.x+rx, this._center.y);
    this._top =    new ImmutableCord(this._center.x,    this._center.y - ry);
    this._bottom = new ImmutableCord(this._center.x,    this._center.y + ry);
  }  
  get id() {
    return this._id;
  }
  get size() {
    return this._size;
  }
  get center() {
    return this._center;
  }
  get left() {
    return this._left;
  }
  get right() {
    return this._right;
  }
  get top() {
    return this._top;
  }
  get bottom() {
    return this._bottom;
  }
}


class ShapeViewVisitor implements Ast.ShapeVisitor {
  private props: Ast.ShapeViewProps;

  constructor(props: Ast.ShapeViewProps) {
    this.props = props;
  }

  visitRoot(root: Ast.RootNode): Ast.ShapeView {
    const lines: Ast.Line[] = [];
    const shapes: Record<string, Ast.Shape> = {};
    const ctx = createContext(new RootShape(this.props.start), root);
    const start = this.visitStart(root.start, ctx);
    
    for(const visited of start.children) {
      shapes[visited.id] = visited;  
    }
    shapes[start.shape.id] = start.shape;
    return new ImmutableShapeView(lines, shapes);
  }
  visitStart(node: Ast.StartNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
   const shape = new ImmutableShape(node.id, node.size, this.props.start);
   const children = this.visitChildren(node.children, context.addNode(node, shape)); 
   return { shape, children };
  }
  visitEnd(node: Ast.EndNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
    const center = {x: this.props.start.x, y: this.visitY(node, context)};
    const shape = new ImmutableShape(node.id, node.size, center);
    return { shape, children: [] };
  }
  visitSwitch(node: Ast.SwitchNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
    const center = {x: this.visitX(node, context), y: this.visitY(node, context)};
    const shape = new ImmutableShape(node.id, node.size, center);
    const children: Ast.Shape[] = [];
    let index = 0; 
    const total = node.children.length
    let visited: Ast.VisitedShapes | null = null;
    for(const child of node.children) {
      const next = context.addNode(node, shape, new ImmutableShapeIndex(index++, total, visited?.shape));
      visited = this.vistChild(child, next);
      children.push(...visited.children);
      children.push(visited.shape); 
    }
    return { shape, children };
  }
  visitDecision(node: Ast.DecisionNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
    const center = {x: this.visitX(node, context), y: this.visitY(node, context)};
    const shape = new ImmutableShape(node.id, node.size, center);
    const children = this.visitChildren(node.children, context.addNode(node, shape));
    return { shape, children };
  }
  visitService(node: Ast.ServiceNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
    const center = {x: this.visitX(node, context), y: this.visitY(node, context)};
    const shape = new ImmutableShape(node.id, node.size, center);
    const children = this.visitChildren(node.children, context.addNode(node, shape));
    return { shape, children };
  }
  visitLoop(node: Ast.DecisionNode | Ast.ServiceNode, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
   return {} as Ast.VisitedShapes;
  }
  vistChild(child: Ast.NodeChild, context: Ast.ShapeVisitorContext): Ast.VisitedShapes {
    const root: Ast.RootNode = context.getRoot();
    const target = root.getById(child.id);
    switch(target.type) {
      case "service":       return this.visitService(target as Ast.ServiceNode, context);
      case "service-loop":  return this.visitService(target as Ast.ServiceNode, context);
      case "decision":      return this.visitDecision(target as Ast.DecisionNode, context);
      case "decision-loop": return this.visitDecision(target as Ast.DecisionNode, context);
      case "switch":        return this.visitSwitch(target as Ast.SwitchNode, context);
      case "end":           return this.visitEnd(target as Ast.EndNode, context);
      default: throw new Error("Can't visit node of type: " + target.type + "!") 
    }
  }
  visitChildren(child: Ast.NodeChild, context: Ast.ShapeVisitorContext): Ast.Shape[] {
    const result: Ast.VisitedShapes = this.vistChild(child, context);
    return [result.shape, ...result.children];
  }
  visitX(node: Ast.Node, context: Ast.ShapeVisitorContext): number {
    if(context.index.total > 1) {
      return this.visitXN(node, context);
    }
    return context.shape.center.x;
  }
  visitY(node: Ast.Node, context: Ast.ShapeVisitorContext): number {
    const shape = context.shape;
    return shape.bottom.y + this.props.sy;
  }
  visitXN(node: Ast.Node, context: Ast.ShapeVisitorContext): number {
    
    const shape = context.shape;
    const start = shape.bottom.x - this.props.mx*context.index.total/2;
    const current = start + this.props.mx/2 + context.index.value*this.props.mx;
    
    /*
    if(context.index.total % 2 === 0) {
      const total = this.props.mx * context.index.value;
      const x = total + node.size.width/2;
      const shape = context.shape;
      return shape.bottom.y + (x);      
    }
    const total = this.props.mx * context.index.value;
    const x = total + node.size.width/2;
    
    return shape.bottom.y + (x);
    */
    return current;
  }
}


export default ShapeViewVisitor;