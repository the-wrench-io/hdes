package io.resys.hdes.ast.spi.visitors.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.immutables.value.Value;

import io.resys.hdes.ast.DecisionTableParser;
import io.resys.hdes.ast.DecisionTableParser.AllContext;
import io.resys.hdes.ast.DecisionTableParser.DescriptionContext;
import io.resys.hdes.ast.DecisionTableParser.DirectionTypeContext;
import io.resys.hdes.ast.DecisionTableParser.DtContext;
import io.resys.hdes.ast.DecisionTableParser.FirstContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderArgsContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderContext;
import io.resys.hdes.ast.DecisionTableParser.HeaderTypeContext;
import io.resys.hdes.ast.DecisionTableParser.HeadersContext;
import io.resys.hdes.ast.DecisionTableParser.HitPolicyContext;
import io.resys.hdes.ast.DecisionTableParser.IdContext;
import io.resys.hdes.ast.DecisionTableParser.LiteralContext;
import io.resys.hdes.ast.DecisionTableParser.MatrixContext;
import io.resys.hdes.ast.DecisionTableParser.RulesContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetContext;
import io.resys.hdes.ast.DecisionTableParser.RulesetsContext;
import io.resys.hdes.ast.DecisionTableParser.TypeNameContext;
import io.resys.hdes.ast.DecisionTableParser.UndefinedValueContext;
import io.resys.hdes.ast.DecisionTableParser.ValueContext;
import io.resys.hdes.ast.DecisionTableParserBaseVisitor;
import io.resys.hdes.ast.api.AstNodeException;
import io.resys.hdes.ast.api.nodes.AstNode;
import io.resys.hdes.ast.api.nodes.AstNode.Literal;
import io.resys.hdes.ast.api.nodes.AstNode.ScalarType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DecisionTableBody;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.DirectionType;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Header;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Headers;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicy;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyAll;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyFirst;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.HitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.Rule;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleRow;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.RuleValue;
import io.resys.hdes.ast.api.nodes.DecisionTableNode.UndefinedValue;
import io.resys.hdes.ast.api.nodes.ImmutableDecisionTableBody;
import io.resys.hdes.ast.api.nodes.ImmutableHeader;
import io.resys.hdes.ast.api.nodes.ImmutableHeaders;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyAll;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyFirst;
import io.resys.hdes.ast.api.nodes.ImmutableHitPolicyMatrix;
import io.resys.hdes.ast.api.nodes.ImmutableLiteral;
import io.resys.hdes.ast.api.nodes.ImmutableLiteralValue;
import io.resys.hdes.ast.api.nodes.ImmutableRule;
import io.resys.hdes.ast.api.nodes.ImmutableRuleRow;
import io.resys.hdes.ast.api.nodes.ImmutableUndefinedValue;
import io.resys.hdes.ast.spi.visitors.ast.Nodes.TokenIdGenerator;

public class DtParserAstNodeVisitor extends DecisionTableParserBaseVisitor<AstNode> {
  private final TokenIdGenerator tokenIdGenerator;

  public DtParserAstNodeVisitor(TokenIdGenerator tokenIdGenerator) {
    super();
    this.tokenIdGenerator = tokenIdGenerator;
  }

  // Internal only
  @Value.Immutable
  public interface RedundentId extends DecisionTableNode {
    String getValue();
  }

  @Value.Immutable
  public interface RedundentDescription extends DecisionTableNode {
    String getValue();
  }

  @Value.Immutable
  public interface RedundentHeaderType extends DecisionTableNode {
    ScalarType getValue();
  }
  
  @Value.Immutable
  public interface RedundentTypeName extends DecisionTableNode {
    String getValue();
  }
  
  @Value.Immutable
  public interface RedundentDirection extends DecisionTableNode {
    DirectionType getValue();
  }

  @Value.Immutable
  public interface RedundentRulesets extends DecisionTableNode {
    List<RuleRow> getRows();
  }

  @Override
  public DecisionTableBody visitDt(DtContext ctx) {
    Nodes children = nodes(ctx);
    return ImmutableDecisionTableBody.builder()
        .token(token(ctx))
        .id(children.of(RedundentId.class).get().getValue())
        .description(children.of(RedundentDescription.class).map(e -> e.getValue()).orElse(null))
        .headers(children.of(Headers.class).get())
        .hitPolicy(children.of(HitPolicy.class).get())
        .build();
  }

  @Override
  public Literal visitLiteral(LiteralContext ctx) {
    ScalarType type = null;
    TerminalNode terminalNode = (TerminalNode) ctx.getChild(0);
    switch (terminalNode.getSymbol().getType()) {
    case DecisionTableParser.StringLiteral:
      type = ScalarType.STRING;
      break;
    case DecisionTableParser.BooleanLiteral:
      type = ScalarType.BOOLEAN;
      break;
    case DecisionTableParser.DecimalLiteral:
      type = ScalarType.DECIMAL;
      break;
    case DecisionTableParser.IntegerLiteral:
      type = ScalarType.INTEGER;
      break;
    default:
      throw new AstNodeException("Unknown literal: " + ctx.getText() + "!");
    }
    return ImmutableLiteral.builder()
        .token(token(ctx))
        .type(type)
        .value(ctx.getText())
        .build();
  }

  @Override
  public UndefinedValue visitUndefinedValue(UndefinedValueContext ctx) {
    return ImmutableUndefinedValue.builder().token(token(ctx)).build();
  }

  @Override
  public RuleValue visitValue(ValueContext ctx) {
    AstNode node = first(ctx);
    return node instanceof RuleValue ? (RuleValue) node
        : ImmutableLiteralValue.builder()
            .token(token(ctx))
            .value((Literal) node)
            .build();
  }

  @Override
  public RedundentHeaderType visitHeaderType(HeaderTypeContext ctx) {
    return ImmutableRedundentHeaderType.builder()
        .token(token(ctx))
        .value(ScalarType.valueOf(ctx.getText()))
        .build();
  }

  @Override
  public HitPolicy visitHitPolicy(HitPolicyContext ctx) {
    return nodes(ctx).of(HitPolicy.class).get();
  }

  @Override
  public HitPolicyFirst visitFirst(FirstContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyFirst.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyAll visitAll(AllContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyAll.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public HitPolicyMatrix visitMatrix(MatrixContext ctx) {
    List<RuleRow> rulesets = nodes(ctx).of(RedundentRulesets.class).map(e -> e.getRows()).orElse(Collections.emptyList());
    return ImmutableHitPolicyMatrix.builder()
        .token(token(ctx))
        .rows(rulesets)
        .build();
  }

  @Override
  public RedundentRulesets visitRulesets(RulesetsContext ctx) {
    return ImmutableRedundentRulesets.builder()
        .token(token(ctx))
        .rows(nodes(ctx).list(RuleRow.class).stream()
            .filter(r -> !r.getRules().isEmpty())
            .collect(Collectors.toList()))
        .build();
  }

  @Override
  public RuleRow visitRuleset(RulesetContext ctx) {
    if (ctx.getChildCount() == 3) {
      return (RuleRow) ctx.getChild(1).accept(this);
    }
    return ImmutableRuleRow.builder().token(token(ctx)).build();
  }

  @Override
  public RuleRow visitRules(RulesContext ctx) {
    List<Rule> rules = new ArrayList<>();
    int n = ctx.getChildCount();
    for (int i = 0; i < n; i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode) {
        continue;
      }
      RuleValue childResult = (RuleValue) c.accept(this);
      rules.add(ImmutableRule.builder()
          .token(childResult.getToken())
          .header(i)
          .value(childResult)
          .build());
    }
    return ImmutableRuleRow.builder()
        .token(token(ctx))
        .rules(rules)
        .build();
  }

  @Override
  public AstNode visitHeaders(HeadersContext ctx) {
    return nodes(ctx).of(Headers.class)
        .orElse(ImmutableHeaders.builder().token(token(ctx)).build());
  }

  @Override
  public Headers visitHeaderArgs(HeaderArgsContext ctx) {
    return ImmutableHeaders.builder()
        .token(token(ctx))
        .values(nodes(ctx).list(Header.class))
        .build();
  }

  @Override
  public Header visitHeader(HeaderContext ctx) {
    Nodes nodes = nodes(ctx);
    return ImmutableHeader.builder()
        .token(token(ctx))
        .name(nodes.of(RedundentTypeName.class).get().getValue())
        .type(nodes.of(RedundentHeaderType.class).get().getValue())
        .direction(nodes.of(RedundentDirection.class).get().getValue())
        .build();
  }

  @Override
  public RedundentId visitId(IdContext ctx) {
    return ImmutableRedundentId.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(RedundentTypeName.class).get().getValue())
        .build();
  }

  @Override
  public RedundentDescription visitDescription(DescriptionContext ctx) {
    return ImmutableRedundentDescription.builder()
        .token(token(ctx))
        .value(nodes(ctx).of(Literal.class).get().getValue())
        .build();
  }

  @Override
  public RedundentDirection visitDirectionType(DirectionTypeContext ctx) {
    return ImmutableRedundentDirection.builder()
        .token(token(ctx))
        .value(DirectionType.valueOf(ctx.getText()))
        .build();
  }

  @Override
  public RedundentTypeName visitTypeName(TypeNameContext ctx) {
    return ImmutableRedundentTypeName.builder()
        .token(token(ctx))
        .value(ctx.getText())
        .build();
  }

  private AstNode first(ParserRuleContext ctx) {
    ParseTree c = ctx.getChild(0);
    return c.accept(this);
  }

  private Nodes nodes(ParserRuleContext node) {
    return Nodes.from(node, this);
  }

  private AstNode.Token token(ParserRuleContext node) {
    return Nodes.token(node, tokenIdGenerator);
  }
}
