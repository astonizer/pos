<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="invoice-page" page-height="11in" page-width="8.5in">
                    <fo:region-body margin-top="1in" margin-bottom="1in" margin-left="1in" margin-right="1in"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="invoice-page">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="16pt" font-weight="bold" text-align="center">
                        Invoice
                    </fo:block>
                    <fo:block font-size="12pt" font-weight="bold" margin-top="0.5in">
                        Order ID:
                        <xsl:value-of select="invoice/orderId"/>
                    </fo:block>
                    <fo:block font-size="12pt">
                        Order name:
                        <xsl:value-of select="invoice/customer"/>
                    </fo:block>
                    <fo:block font-size="12pt">
                        Invoice Date:
                        <xsl:value-of select="invoice/invoiceDate"/>
                    </fo:block>
                    <fo:block font-size="12pt">
                        Invoice Time:
                        <xsl:value-of select="invoice/invoiceTime"/>
                    </fo:block>
                    <fo:block margin-top="0.5in">
                        <fo:table table-layout="fixed" width="100%" border-collapse="collapse">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="15%"/>
                            <fo:table-column column-width="20%"/>
                            <fo:table-header>
                                <fo:table-row font-weight="bold">
                                    <fo:table-cell border="1pt solid black">
                                        <fo:block>Name</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black">
                                        <fo:block>Quantity</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black">
                                        <fo:block>Price</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black">
                                        <fo:block>Amount</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-footer>
                                <fo:table-row font-weight="bold">
                                    <fo:table-cell number-columns-spanned="3" border="1pt solid black">
                                        <fo:block>Total</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="invoice/total"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-footer>
                            <fo:table-body>
                                <xsl:apply-templates select="invoice/orderItems/orderItem"/>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block margin-top="1cm"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="orderItem">
        <fo:table-row>
            <fo:table-cell border="1pt solid black">
                <fo:block>
                    <xsl:value-of select="description"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" text-align="center">
                <fo:block>
                    <xsl:value-of select="quantity"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" text-align="right">
                <fo:block>
                    <xsl:value-of select="price"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1pt solid black" text-align="right">
                <fo:block>
                    <xsl:value-of select="amount"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

</xsl:stylesheet>

